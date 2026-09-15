export type Uuid = string;

export type CampaignRole = "DM" | "PLAYER";
export type CampaignMembershipStatus = "ACTIVE" | "KICKED" | "BANNED";

export interface AccountIdentityDto {
  id: Uuid;
  externalSubject: string;
  displayName?: string | null;
}

export interface CampaignMembershipDto {
  campaignId: Uuid;
  accountId: Uuid;
  role: CampaignRole;
  status: CampaignMembershipStatus;
}

export interface PcAuthorityDto {
  pcId: Uuid;
  ownerAccountId?: Uuid | null;
  controllerAccountId?: Uuid | null;
}

export interface MutationEnvelope<TPayload> {
  mutationId: Uuid;
  objectId: Uuid;
  expectedRevision: number;
  payload: TPayload;
}

export interface MutationResult<TState> {
  objectId: Uuid;
  revision: number;
  state: TState;
}

export type ApiErrorCode =
  | "UNAUTHENTICATED"
  | "FORBIDDEN"
  | "NOT_FOUND"
  | "VALIDATION_FAILED"
  | "CONFLICT_STALE_REVISION"
  | "CONFLICT_MUTATION_REUSE"
  | "GONE"
  | "TRANSIENT_FAILURE"
  | "INTERNAL_ERROR";

export interface ApiErrorBody {
  code: ApiErrorCode;
  message: string;
  details?: Record<string, unknown>;
}

export function assertNonNegativeRevision(revision: number): void {
  if (!Number.isSafeInteger(revision) || revision < 0) {
    throw new Error("Revision must be a non-negative safe integer.");
  }
}
