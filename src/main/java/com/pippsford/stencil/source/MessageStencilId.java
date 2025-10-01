package com.pippsford.stencil.source;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;

import com.pippsford.stencil.escape.Escape;

/**
 * A stencil derived from a message in a resource bundle.
 *
 * @author Simon Greatrix on 10/01/2021.
 */
public class MessageStencilId extends StencilId {

  private final String messageId;


  /**
   * New message stencil identifier.
   *
   * @param bundle    the bundle that contains this stencil
   * @param messageId the name of this stencil
   * @param locale    the processing locale
   * @param escape    the escape mode
   */
  public MessageStencilId(String bundle, String messageId, Locale locale, Escape escape) {
    super(locale, bundle, escape);
    Objects.requireNonNull(messageId);
    this.messageId = messageId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MessageStencilId)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    MessageStencilId that = (MessageStencilId) o;

    return getMessageId().equals(that.getMessageId());
  }


  @Override
  public String getLogId() {
    return messageId;
  }


  /**
   * Get the message ID in the resource bundle.
   *
   * @return the message ID
   */
  public String getMessageId() {
    return messageId;
  }


  @Override
  @Nonnull
  public String getText() throws StencilNotFoundException {
    try {
      return ResourceBundle.getBundle(getBundle(), getLocale()).getString(getMessageId());
    } catch (MissingResourceException e) {
      throw new StencilNotFoundException("Stencil for " + getMessageId() + " in bundle " + getBundle() + " was not found", e);
    }
  }


  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + getMessageId().hashCode();
    return result;
  }


  @Override
  public boolean isMessage() {
    return true;
  }

}
