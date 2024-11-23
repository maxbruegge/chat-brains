import { Request, Response, NextFunction } from 'express';
import ffmpeg from 'fluent-ffmpeg';
import axios, { AxiosError } from 'axios';
import fs from 'fs';
import path from 'path';
import { aiService } from '../ai/ai.service';

const GOOGLE_API_KEY = process.env.GOOGLE_API_KEY;

class AiController {
  async processAudio(req: Request, res: Response): Promise<void> {
    if (!req.file) {
      res
        .status(400)
        .json({ success: false, message: 'No audio file uploaded.' });
      return;
    }

    try {
      const buffer = Buffer.from(req.file.buffer);

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

      const result = await aiService.runAI(transcription);
      res.status(200).json({
        success: true,
        answer: result?.content.toString(),
      });

      res.status(200).json({ success: true, transcription });
    } catch (error) {
      const axiosError = error as AxiosError;
      console.error('Error processing audio:', axiosError.message);
      res.status(500).json({
        success: false,
        message: 'Error processing audio.',
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
          console.log('FFmpeg conversion completed.');
          const wavBuffer = fs.readFileSync(outputFilePath);
          resolve(wavBuffer);
        })
        .save(outputFilePath); // Save WAV to disk
    });
  }
}

export const aiController = new AiController();
