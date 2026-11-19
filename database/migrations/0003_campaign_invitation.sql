-- Owner, 2026-11-18 («A: invitar jugadores»): a campaign could be moderated but nobody could join
-- it. An invitation is one short code per campaign, with an expiry the DM can see, that any signed
-- in account may redeem once to become a PLAYER. PRODUCT.md §6 already describes this enrollment.

CREATE TABLE campaign_invitation (
    code text PRIMARY KEY CHECK (code ~ '^[A-Z]{6}$'),
    campaign_id uuid NOT NULL REFERENCES campaign(id) ON DELETE CASCADE,
    created_by uuid NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    created_at timestamptz NOT NULL DEFAULT now(),
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz,
    CHECK (expires_at > created_at)
);

-- One live invitation per campaign at a time: a new one replaces the old.
CREATE UNIQUE INDEX campaign_invitation_live_idx
    ON campaign_invitation(campaign_id)
    WHERE revoked_at IS NULL;
