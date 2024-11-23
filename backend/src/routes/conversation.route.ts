// src/routes/userRoutes.ts
import { Router } from 'express';
import { conversationController } from '../controllers/conversation.controller';
import { authMiddleware } from '../middlewares/auth.middleware';

const router = Router();

router.get('/', authMiddleware, conversationController.getConversation);
router.post('/', authMiddleware, conversationController.createConversation);
router.post('/:id', authMiddleware, conversationController.addToConversation);

export { router as conversationRoutes };
