// src/index.ts
import express from 'express';
import bodyParser from 'body-parser';
import dotenv from 'dotenv';
import connectDB from './config/db';
import router from './routes/routes';
import { errorHandler } from './utils/error-handler';
import './web-socket';

dotenv.config();
connectDB();

const app = express();

app.use(bodyParser.json());
app.use('/api', router);
app.use(errorHandler);

const PORT = process.env.PORT || 8000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
