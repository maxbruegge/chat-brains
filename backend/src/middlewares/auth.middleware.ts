import { Request, Response, NextFunction } from 'express';
import jwt, { JwtPayload } from 'jsonwebtoken';
import { Types } from 'mongoose';
import { githubAdapter } from '../adapters/github.adapter';
import { githubController } from '../controllers/github.controller';

interface AuthenticatedRequest extends Request {
  user: { email: string; id: Types.ObjectId }; // Extend Request to include user details
}

export const authMiddleware = (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
): void => {
  const authHeader = req.headers.authorization;

  // Check if the Authorization header is present and has a Bearer token
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    res
      .status(401)
      .json({ success: false, message: 'Unauthorized: No token provided' });
    return; // Explicitly return to stop execution
  }

  const token = authHeader.split(' ')[1]; // Extract the token after "Bearer"

  try {
    // Verify the token using your secret key
    const secret = process.env.JWT_SECRET || 'your_jwt_secret';
    const decoded = jwt.verify(token, secret) as JwtPayload;

    // Ensure the payload contains an email
    if (!decoded.email || !decoded.id) {
      res
        .status(401)
        .json({ success: false, message: 'Invalid token payload' });
      return; // Explicitly return to stop execution
    }

    // Attach the user's email and id to the request object
    req.user = { email: decoded.email, id: decoded.id };

    next(); // Call the next middleware or route handler
  } catch (err) {
    res
      .status(401)
      .json({ success: false, message: 'Unauthorized: Invalid token' });
  }
};
