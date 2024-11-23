// src/routes/userRoutes.ts
import { Router } from 'express';
import { createConversation, getConversation } from '../controllers/conversation.controller';
import { authMiddleware } from '../middlewares/auth.middleware';

const router = Router();

router.get('/', getConversation);
router.post('/', authMiddleware, createConversation);

export { router as conversationRoutes };
