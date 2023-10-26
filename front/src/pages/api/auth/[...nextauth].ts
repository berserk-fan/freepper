import NextAuth, { AuthOptions } from "next-auth";
import GithubProvider from "next-auth/providers/github";

const githubProvider = process.env.GITHUB_CLIENT_ID
  ? [
      GithubProvider({
        clientId: process.env.GITHUB_CLIENT_ID,
        clientSecret: process.env.GITHUB_CLIENT_SECRET,
      }),
    ]
  : [];
export const authOptions: AuthOptions = {
  // Configure one or more authentication providers
  providers: githubProvider,
  secret: process.env.NEXTAUTH_SECRET,
  session: { strategy: "jwt" },
};

export default NextAuth(authOptions);
