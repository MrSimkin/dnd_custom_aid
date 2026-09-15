import { createRemoteJWKSet, jwtVerify, type JWTPayload } from "jose";

export interface AuthenticatedIdentity {
  subject: string;
  displayName: string | null;
  claims: JWTPayload;
}

export interface AuthVerifier {
  verify(request: Request): Promise<AuthenticatedIdentity>;
}

export class AuthenticationError extends Error {
  constructor(message = "Authentication required.") {
    super(message);
    this.name = "AuthenticationError";
  }
}

export function extractBearerToken(request: Request): string {
  const header = request.headers.get("authorization");
  if (header == null) {
    throw new AuthenticationError();
  }

  const match = /^Bearer\s+([^\s]+)$/i.exec(header.trim());
  if (match == null || match[1].length === 0) {
    throw new AuthenticationError("A valid Bearer token is required.");
  }

  return match[1];
}

export interface DescopeJwtVerifierOptions {
  projectId: string;
  baseUrl?: string;
}

export class DescopeJwtVerifier implements AuthVerifier {
  private readonly projectId: string;
  private readonly jwks: ReturnType<typeof createRemoteJWKSet>;

  constructor(options: DescopeJwtVerifierOptions) {
    const projectId = options.projectId.trim();
    if (projectId.length === 0) {
      throw new Error("DESCOPE_PROJECT_ID must not be blank.");
    }

    const baseUrl = (options.baseUrl ?? "https://api.descope.com").trim().replace(/\/+$/, "");
    if (!/^https:\/\//i.test(baseUrl)) {
      throw new Error("Descope base URL must use HTTPS.");
    }

    this.projectId = projectId;
    this.jwks = createRemoteJWKSet(
      new URL(`${baseUrl}/${encodeURIComponent(projectId)}/.well-known/jwks.json`),
    );
  }

  async verify(request: Request): Promise<AuthenticatedIdentity> {
    const token = extractBearerToken(request);

    try {
      const { payload } = await jwtVerify(token, this.jwks, {
        audience: this.projectId,
      });
      const subject = payload.sub?.trim();
      if (subject == null || subject.length === 0) {
        throw new AuthenticationError("Authenticated token is missing its subject.");
      }

      const displayName = typeof payload.name === "string" && payload.name.trim().length > 0
        ? payload.name.trim()
        : null;

      return {
        subject,
        displayName,
        claims: payload,
      };
    } catch (error) {
      if (error instanceof AuthenticationError) {
        throw error;
      }
      throw new AuthenticationError("Session token could not be validated.");
    }
  }
}
