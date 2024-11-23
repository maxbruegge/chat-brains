import User, { IUser } from '../models/user';
import { Error as MongooseError, Types } from 'mongoose';

class UserRepository {
  /**
   * Inserts a new user into the database.
   * @param userData - Partial user object (name, email, password).
   * @returns The newly created user document.
   */
  async insertUser(userData: Partial<IUser>): Promise<IUser> {
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
  }

  /**
   * Retrieves a user from the database by ID or email.
   * @param query - An object containing either the user ID or email address.
   * @returns The user document or null if not found.
   */
  async getUserById(id: Types.ObjectId): Promise<IUser | null> {
    try {
      if (id) {
        return await User.findById(id); // Fetch user by ID
      } else {
        throw new Error('Query must include "id".');
      }
    } catch (error: unknown) {
      if (error instanceof MongooseError.CastError) {
        throw new Error(`Invalid ID format: ${error.message}`); // Handle invalid ID format
      }

      throw new Error(`Failed to fetch user: ${error}`);
    }
  }

  /**
   * Fetches a user by email.
   * @param email - User's email address.
   * @returns The user object or null if not found.
   */
  async getUserByEmail(email: string): Promise<IUser | null> {
    return User.findOne({ email });
  }

  /**
   * Fetches a user by GitHub API key.
   * @param githubApiKey - The GitHub API key to search for.
   * @returns The user object or null if not found.
   */
  async getUserByGithubApiKey(githubApiKey: string): Promise<IUser | null> {
    return User.findOne({ githubApiKey });
  }

  /**
   * Updates the API key for a user.
   * @param userId - The ID of the user.
   * @param apiKey - The new API key to set.
   * @returns The updated user document.
   */
  async setGithubApiKey(
    userId: Types.ObjectId,
    githubApiKey: string,
    owner: string,
    repo: string
  ): Promise<IUser | null> {
    return User.findByIdAndUpdate(userId, { githubApiKey, owner, repo });
  }

  /**
   * Retrieves the API key for a user.
   * @param userId - The ID of the user.
   * @returns The user's API key.
   */
  async getApiKey(userId: Types.ObjectId): Promise<string | null> {
    const user = await User.findById(userId);
    return user?.githubApiKey || null;
  }

  /**
   * Updates a user with new data.
   * @param userId - The ID of the user.
   * @param updateData - An object containing the fields to update.
   * @returns The updated user document or null.
   */
  async updateUser(
    userId: string,
    updateData: Partial<IUser>
  ): Promise<IUser | null> {
    return User.findByIdAndUpdate(userId, updateData, { new: true });
  }
}

export const userRepository = new UserRepository();
