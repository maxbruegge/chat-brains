// src/routes/userRoutes.ts
import { Router } from 'express';
import { conversationController } from '../controllers/conversation.controller';
import { authMiddleware } from '../middlewares/auth.middleware';

const router = Router();

router.get('/', conversationController.getConversation);
router.post('/', authMiddleware, conversationController.createConversation);

export { router as conversationRoutes };
