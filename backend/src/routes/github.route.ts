// src/routes/userRoutes.ts
import { Router } from 'express';
import { githubController } from '../controllers/github.controller';
import { authMiddleware } from '../middlewares/auth.middleware';

const router = Router();

router.patch('/', authMiddleware, githubController.setApiKey);
router.get('/branches', authMiddleware, githubController.getAllBranches);
router.get('/repos', authMiddleware, githubController.getAllRepos);

export { router as githubRoutes };
