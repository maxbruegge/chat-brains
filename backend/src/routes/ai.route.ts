// src/routes/userRoutes.ts
import { Router } from 'express';
import { authMiddleware } from '../middlewares/auth.middleware';
import { aiController } from '../controllers/ai.controller';
import uploadMiddleware from '../middlewares/upload.middleware';

const router = Router();

router.post(
  '/',
  authMiddleware,
  uploadMiddleware.single('audio'),
  aiController.processAudio
);

export { router as aiRoutes };
