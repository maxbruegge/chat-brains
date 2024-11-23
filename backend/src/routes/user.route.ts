// src/routes/userRoutes.ts
import { Router } from 'express';
import { userController } from '../controllers/user.controller';

const router = Router();

router.post('/', userController.createUser);
router.post('/sign-in', userController.signIn);

export { router as userRoutes };
