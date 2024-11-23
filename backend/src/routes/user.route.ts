// src/routes/userRoutes.ts
import { Router } from 'express';
import { createUser, getUser, signIn } from '../controllers/user.controller';

const router = Router();

router.post('/', createUser);
router.post('/sign-in', signIn);

export { router as userRoutes };
