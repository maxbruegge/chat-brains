import User, { IUser } from '../models/user';
import { Error as MongooseError } from 'mongoose';

/**
 * Inserts a new user into the database.
 * @param userData - Partial user object (name, email, password).
 * @returns The newly created user document.
 */
export const insertUser = async (userData: Partial<IUser>): Promise<IUser> => {
  try {
    const newUser = new User(userData);
    return await newUser.save();
  } catch (error: unknown) {
    if (error instanceof MongooseError.ValidationError) {
      throw new Error(
        `Validation error: ${Object.values(error.errors)
          .map((err) => err.message)
          .join(', ')}`
      );
    }

    throw new Error(`Failed to insert user: ${error}`);
  }
};

/**
 * Retrieves a user from the database by ID or email.
 * @param query - An object containing either the user ID or email address.
 * @returns The user document or null if not found.
 */
export const getUserById = async (query: {
  id?: string;
  email?: string;
}): Promise<IUser | null> => {
  try {
    if (query.id) {
      // Fetch user by ID
      return await User.findById(query.id);
    } else if (query.email) {
      // Fetch user by email
      return await User.findOne({ email: query.email });
    } else {
      throw new Error('Query must include either "id" or "email".');
    }
  } catch (error: unknown) {
    if (error instanceof MongooseError.CastError) {
      // Handle invalid ID format or casting issues
      throw new Error(`Invalid ID format: ${error.message}`);
    }

    throw new Error(`Failed to fetch user: ${error}`);
  }
};

/**
 * Fetches a user by email.
 * @param email - User's email address.
 * @returns The user object or null if not found.
 */
export const getUserByEmail = async (email: string): Promise<IUser | null> => {
  return User.findOne({ email });
};
