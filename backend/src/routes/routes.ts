import { Router } from 'express';
import { conversationRoutes } from './conversation.route';
import { userRoutes } from './user.route';
import { githubRoutes } from './github.route';
import { aiRoutes } from './ai.route';

const router = Router();

// Mount routes under "/api"
router.use('/user', userRoutes);
router.use('/conversation', conversationRoutes);
router.use('/github', githubRoutes);
router.use('/ai', aiRoutes);

export default router;
