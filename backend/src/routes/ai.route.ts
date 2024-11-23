// src/routes/userRoutes.ts
import { Router } from 'express';
import { authMiddleware } from '../middlewares/auth.middleware';
import { aiController } from '../controllers/ai.controller';
import uploadMiddleware from '../middlewares/upload.middleware';

const router = Router();

router.post(
  '/',
  authMiddleware,
  aiController.processAudio
);
router.post('/text', authMiddleware, aiController.processText);

export { router as aiRoutes };
