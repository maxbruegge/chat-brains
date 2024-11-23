// src/index.ts
import express from 'express';
import bodyParser from 'body-parser';
import dotenv from 'dotenv';
import connectDB from './config/db';
import router from './routes/routes';
import { errorHandler } from './utils/error-handler';
import { AIService } from './ai/ai.service';
import './web-socket';

dotenv.config();
connectDB();

const app = express();

app.use(bodyParser.json());
app.use('/api', router);
app.use(errorHandler);

// TO BE DELETED
const aiService = new AIService();
app.get('/ai', async (req, res) => {
  try {
    const { message } = req.body;
    const result = await aiService.runAI(message);
    res.send(result?.content.toString());
  } catch (error) {
    console.error(error);
    res.status(500).send('An error occurred.');
  }
});

const PORT = process.env.PORT || 8000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
