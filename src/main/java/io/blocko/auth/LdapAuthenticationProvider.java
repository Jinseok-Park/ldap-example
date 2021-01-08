package io.blocko.auth;

import io.blocko.exception.UnauthenticatedUserException;
import io.blocko.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LdapAuthenticationProvider implements AuthenticationProvider {

  private final LdapService ldapService;

  /**
   * 사용자 인증.
   * @param authentication
   * @return
   * @throws AuthenticationException
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String email = authentication.getName();
    String password = (String) authentication.getCredentials();

    boolean isAuthenticated = ldapService.authenticate(email, password);

    if (!isAuthenticated) {
      throw new UnauthenticatedUserException();
    } else {
      LdapUser user = ldapService.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
      return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
  }
}
