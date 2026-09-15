BEGIN;

CREATE TABLE app_user (
    id uuid PRIMARY KEY,
    descope_subject text NOT NULL UNIQUE CHECK (length(btrim(descope_subject)) > 0),
    display_name text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE campaign (
    id uuid PRIMARY KEY,
    name text NOT NULL CHECK (length(btrim(name)) > 0),
    revision bigint NOT NULL DEFAULT 0 CHECK (revision >= 0),
    deleted_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE campaign_membership (
    campaign_id uuid NOT NULL REFERENCES campaign(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role text NOT NULL CHECK (role IN ('DM', 'PLAYER')),
    status text NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'KICKED', 'BANNED')),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (campaign_id, user_id)
);

CREATE INDEX campaign_membership_user_idx
    ON campaign_membership(user_id, status);

CREATE TABLE pc (
    id uuid PRIMARY KEY,
    campaign_id uuid NOT NULL REFERENCES campaign(id) ON DELETE CASCADE,
    owner_user_id uuid REFERENCES app_user(id),
    controller_user_id uuid REFERENCES app_user(id),
    name text NOT NULL CHECK (length(btrim(name)) > 0),
    revision bigint NOT NULL DEFAULT 0 CHECK (revision >= 0),
    deleted_at timestamptz,
    snapshot_format text,
    snapshot_version integer CHECK (snapshot_version IS NULL OR snapshot_version >= 1),
    snapshot jsonb,
    reconciled_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX pc_campaign_idx
    ON pc(campaign_id)
    WHERE deleted_at IS NULL;

CREATE INDEX pc_owner_idx
    ON pc(owner_user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX pc_controller_idx
    ON pc(controller_user_id)
    WHERE deleted_at IS NULL;

-- Idempotency receipts are deliberately small. The backend may later add response material only
-- when a concrete retry flow needs it; mutation identity itself is part of the MVP foundation.
CREATE TABLE mutation_receipt (
    user_id uuid NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    mutation_id uuid NOT NULL,
    object_type text NOT NULL CHECK (length(btrim(object_type)) > 0),
    object_id uuid NOT NULL,
    resulting_revision bigint NOT NULL CHECK (resulting_revision >= 0),
    created_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, mutation_id)
);

COMMIT;
