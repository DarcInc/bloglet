package io.darkink.bloglet.processing.settings;

public class FeatureRepositoryException extends RuntimeException {
    public FeatureRepositoryException(String message) {
        super(message);
    }
    public FeatureRepositoryException(String message, Throwable t) {
        super(message, t);
    }
}
