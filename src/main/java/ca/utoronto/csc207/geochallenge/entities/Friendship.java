package ca.utoronto.csc207.geochallenge.entities;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A connection or friend request between two players.
 */
public final class Friendship {
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED;

        public static Status fromString(String value) {
            if (value == null) return PENDING;
            try {
                return Status.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return PENDING;
            }
        }
    }

    private final UUID requesterId;
    private final UUID addresseeId;
    private final Status status;
    private final Instant createdAt;

    public Friendship(UUID requesterId, UUID addresseeId, Status status, Instant createdAt) {
        this.requesterId = Objects.requireNonNull(requesterId, "Requester ID is required.");
        this.addresseeId = Objects.requireNonNull(addresseeId, "Addressee ID is required.");
        if (requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("Requester and addressee cannot be the same user.");
        }
        this.status = Objects.requireNonNull(status, "Friendship status is required.");
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public UUID getAddresseeId() {
        return addresseeId;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean involves(UUID userId) {
        return requesterId.equals(userId) || addresseeId.equals(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;
        Friendship that = (Friendship) o;
        return requesterId.equals(that.requesterId) &&
                addresseeId.equals(that.addresseeId) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requesterId, addresseeId, status);
    }
}
