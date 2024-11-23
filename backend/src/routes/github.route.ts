// src/routes/userRoutes.ts
import { Router } from 'express';
import { githubController } from '../controllers/github.controller';
import { authMiddleware } from '../middlewares/auth.middleware';

const router = Router();

router.patch('/', authMiddleware, githubController.setApiKey);

export { router as githubRoutes };
