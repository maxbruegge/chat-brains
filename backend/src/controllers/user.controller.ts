import { Request, Response, NextFunction } from 'express';
import {
  getUserByEmail,
  getUserById,
  insertUser,
} from '../repositories/user.repository';
import jwt from 'jsonwebtoken';

/**
 * Creates a new user.
 * @param req - Express request object.
 * @param res - Express response object.
 * @param next - Express next middleware function.
 */
export const createUser = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { username, email, password } = req.body;

    if (!username || !email || !password) {
      throw new Error('Name, email, and password are required fields.');
    }

    const newUser = await insertUser({ username, email, password });
    res.status(201).json({ success: true, data: newUser });
  } catch (error) {
    next(error); // Pass the error to the global error handler
  }
};

/**
 * Retrieves a user by ID or email.
 * @param req - Express request object.
 * @param res - Express response object.
 * @param next - Express next middleware function.
 */
export const getUser = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id, email } = req.query;

    if (!id && !email) {
      return res
        .status(400)
        .json({ success: false, message: 'ID or email is required.' });
    }

    const user = await getUserById({
      id: id as string,
      email: email as string,
    });
    if (!user) {
      return res
        .status(404)
        .json({ success: false, message: 'User not found.' });
    }

    res.status(200).json({ success: true, data: user });
  } catch (error) {
    next(error); // Pass the error to the global error handler
  }
};

const JWT_SECRET = process.env.JWT_SECRET || 'your_jwt_secret';
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '1h';

/**
 * Sign-In Controller
 * Validates user credentials and issues a JWT.
 */
export const signIn = async (
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const { email, password } = req.body;

    // Validate input
    if (!email || !password) {
      res
        .status(400)
        .json({ success: false, message: 'Email and password are required.' });
      return;
    }

    // Fetch user by email
    const user = await getUserByEmail(email);

    if (!user) {
      res.status(401).json({ success: false, message: 'Invalid credentials.' });
      return;
    }

    // Compare password
    const isPasswordValid = user.password === password;

    if (!isPasswordValid) {
      res.status(401).json({ success: false, message: 'Invalid credentials.' });
      return;
    }

    // Generate JWT
    const token = jwt.sign({ id: user._id, email: user.email }, JWT_SECRET, {
      expiresIn: JWT_EXPIRES_IN,
    });

    // Respond with the token
    res.status(200).json({ success: true, token });
  } catch (error) {
    next(error); // Pass errors to the global error handler
  }
};
