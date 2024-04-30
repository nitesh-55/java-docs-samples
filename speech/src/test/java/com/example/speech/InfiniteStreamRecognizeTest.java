package com.example.speech;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InfiniteStreamRecognizeTest {

  @Test
    public void testZeroMillis() {
        String testZeroMillis = convertMillisToDate(0.0);
        assertEquals("00:00/", testZeroMillis);
         String largeValue = convertMillisToDate(3600000.0); // 1 hour
        assertEquals("60:00/", largeValue);
       String doubleValue = convertMillisToDate(123456.789);
        assertEquals("02:03/", doubleValue);
    }

@Test
    public void testInfiniteStreamingRecognize() throws Exception {
        SpeechClient mockClient = Mockito.mock(SpeechClient.class);
        ClientStream<StreamingRecognizeRequest> mockClientStream = Mockito.mock(ClientStream.class);
        StreamController mockStreamController = Mockito.mock(StreamController.class);

        // Mock client behavior
        Mockito.when(mockClient.streamingRecognizeCallable()).thenReturn((StreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse>) mock(StreamingCallable.class));
        Mockito.when(mockClient.streamingRecognizeCallable().splitCall(any())).thenReturn(mockClientStream);

        // Mock client stream behavior
        Mockito.doNothing().when(mockClientStream).closeSend();
        Mockito.when(mockClientStream.send(any())).thenReturn(null);

        // Mock stream controller behavior
        doNothing().when(mockStreamController).cancel();

        SpeechService speechService = new SpeechService();
        speechService.setSpeechClient(mockClient);

        speechService.infiniteStreamingRecognize("en-US");

        Mockito.verify(mockClient, Mockito.times(1)).streamingRecognizeCallable();
        Mockito.verify(mockClient.streamingRecognizeCallable(), Mockito.times(1)).splitCall(any());
        Mockito.verify(mockClientStream, Mockito.atLeastOnce()).send(any());
        Mockito.verify(mockClientStream, Mockito.atLeastOnce()).closeSend();
        Mockito.verify(mockStreamController, Mockito.atLeastOnce()).cancel();
    }
}
