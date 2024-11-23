import { Request, Response, NextFunction } from 'express';

/**
 * Global error handler for Express.
 */
export const errorHandler = (
  err: any,
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const statusCode = err.status || 500; // Default to 500 for server errors
  const message = err.message || 'Internal Server Error';

  // Log the error for debugging
  console.error(`[ERROR]: ${message}`);

  // Ensure response is always JSON
  res.status(statusCode).json({
    success: false,
    message,
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack }), // Include stack trace in development
  });
};
