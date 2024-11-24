import { Request, Response, NextFunction } from 'express';
import ffmpeg from 'fluent-ffmpeg';
import axios, { AxiosError } from 'axios';
import fs from 'fs';
import path from 'path';
import { aiService } from '../ai/ai.service';
import OpenAI from 'openai';

const GOOGLE_API_KEY = process.env.GOOGLE_API_KEY;

class AiController {
  async processAudio(req: Request, res: Response): Promise<void> {
    if (!req.body.file) {
      res
        .status(400)
        .json({ success: false, message: 'No audio file uploaded.' });
      return;
    }

    try {
      // Decode Base64 string into a buffer
      const base64String = req.body.file; // Assuming the Base64 string is sent in `req.body.file`
      const buffer = Buffer.from(base64String, 'base64');

      // Convert MP4 to WAV
      const audioBuffer = await aiController.convertMp4ToWav(buffer);

      // Encode WAV buffer to Base64
      const audioBytes = audioBuffer.toString('base64');

      // Prepare Google Speech-to-Text API request
      const request = {
        config: {
          encoding: 'LINEAR16',
          sampleRateHertz: 16000,
          languageCode: 'en-US',
        },
        audio: {
          content: audioBytes,
        },
      };

      // Send transcription request
      const response = await axios.post(
        `https://speech.googleapis.com/v1/speech:recognize?key=${GOOGLE_API_KEY}`,
        request,
        { headers: { 'Content-Type': 'application/json' } }
      );

      // Check transcription results
      if (!response.data.results || response.data.results.length === 0) {
        res.status(400).json({
          success: false,
          transcription: 'No transcription available.',
          message: 'Speech not detected or low-quality audio.',
        });
        return;
      }

      // Extract transcription
      const transcription =
        response.data.results
          .map((result: any) => result.alternatives?.[0]?.transcript)
          .join(' ') || 'No transcription available.';
      console.log('Transcription:', transcription);
      // Process transcription with AI service
      const result = await aiService.runAI({
        message: transcription,
        userId: req.user.id,
      });

      console.log('AI response:', result?.content.toString());

      const file = await generateAudio(result?.content.toString());

      res.status(200).json({
        success: true,
        answer: result?.content.toString(),
        file: file,
      });
      return
    } catch (error) {
      const axiosError = error as AxiosError;
      console.error('Error processing audio ):', axiosError.message);
      res.status(500).json({
        success: false,
        message: 'Error processing audio. ):',
        error: axiosError.message,
      });
    }
  }

  async convertMp4ToWav(buffer: Buffer): Promise<Buffer> {
    // Ensure the temp directory exists
    const tempDir = path.resolve('./temp');
    if (!fs.existsSync(tempDir)) {
      fs.mkdirSync(tempDir);
    }
    const inputFilePath = path.resolve('./temp', 'input-temp.mp4');
    const outputFilePath = path.resolve('./temp', 'output-temp.wav');

    // Save the buffer to disk
    fs.writeFileSync(inputFilePath, buffer);

    return new Promise((resolve, reject) => {
      ffmpeg(inputFilePath)
        .audioCodec('pcm_s16le') // Convert to Linear PCM
        .audioFrequency(16000) // Resample to 16,000 Hz
        .audioChannels(1) // Ensure mono audio
        .toFormat('wav')
        .on('start', (cmd) => console.log(`FFmpeg Command: ${cmd}`))
        .on('stderr', (line) => console.error(`FFmpeg stderr: ${line}`))
        .on('error', (err) => {
          console.error('FFmpeg error:', err.message);
          reject(err);
        })
        .on('end', () => {
          const wavBuffer = fs.readFileSync(outputFilePath);
          resolve(wavBuffer);
        })
        .save(outputFilePath); // Save WAV to disk
    });
  }

  async processText(req: Request, res: Response): Promise<void> {
    const { message } = req.body;

    try {
      const result = await aiService.runAI({
        message: message,
        userId: req.user.id,
      });

      const file = await generateAudio(result?.content.toString());

      res.status(200).json({
        success: true,
        answer: result?.content.toString(),
        file: file,
      });
    } catch (error) {
      if (error instanceof AxiosError) {
      console.error('Error processing audio:', error.message);
      res.status(500).json({
        success: false,
        message: 'Error processing audio.',
        error: error.message,
      });
    }
    }
  }
}

async function generateAudio(aiMessage: string): Promise<string> {
  try {
    const openai = new OpenAI({
      apiKey: process.env.OPENAI_API_KEY,
    });

    const response = await openai.audio.speech.create({
      model: 'tts-1',
      input: aiMessage,
      voice: 'alloy',
      speed: 1.3,
    });

    // Convert the response to an ArrayBuffer
    const audioArrayBuffer = await response.arrayBuffer();

    // Create a Buffer from the ArrayBuffer
    const audioBuffer = Buffer.from(audioArrayBuffer);

    // Convert the Buffer to a Base64-encoded string
    const base64Audio = audioBuffer.toString('base64');

    console.log('Base64 audio string generated.');
    return base64Audio;
  } catch (error) {
    console.error('Error generating audio:', error);
    throw error;
  }
}

export const aiController = new AiController();
