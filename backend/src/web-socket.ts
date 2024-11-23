import WebSocket, { WebSocketServer } from 'ws';
import { SpeechClient } from '@google-cloud/speech';
import { Transform } from 'stream';

const PORT = 5001;

// Initialize WebSocket server
const wss = new WebSocketServer({ port: PORT });
console.log(`WebSocket server started on ws://localhost:${PORT}`);

// Initialize Google Cloud Speech-to-Text client
const speechClient = new SpeechClient();

// Transform stream to process audio chunks
class AudioStreamTransform extends Transform {
  _transform(chunk: any, encoding: BufferEncoding, callback: Function) {
    this.push(chunk);
    callback();
  }
}

// WebSocket server connection handler
wss.on('connection', (ws: WebSocket) => {
  console.log('Client connected');

  let recognizeStream: any;

  ws.on('message', async (message: Buffer) => {
    // Initialize recognition stream on first message
    if (!recognizeStream) {
      recognizeStream = speechClient
        .streamingRecognize({
          config: {
            encoding: 'LINEAR16', // Audio format (e.g., PCM 16-bit)
            sampleRateHertz: 16000, // Sampling rate
            languageCode: 'en-US', // Language
          },
        })
        .on('error', (error: any) => {
          console.error('Speech-to-Text Error:', error);
          ws.close();
        })
        .on('data', (data: any) => {
          const transcription =
            data.results[0]?.alternatives[0]?.transcript || '';
          console.log('Transcription:', transcription);
          ws.send(JSON.stringify({ transcript: transcription })); // Send real-time transcription to the client
        });
    }

    // Write incoming audio data to the recognition stream
    recognizeStream.write(message);
  });

  ws.on('close', () => {
    console.log('Client disconnected');
    if (recognizeStream) {
      recognizeStream.end();
    }
  });
});
