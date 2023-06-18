package au.com.ontheboat.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"ownedBoats", "changeOwnerRequests"})
@ToString
public class Crew implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    @ToString.Exclude
    private UUID uuid;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String email;
    @Column
    private String mobile;
    @Column
    private String username;
    @Column
    @JsonIgnore
    private String password;
    @ToString.Exclude
    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "boat_owners",
            joinColumns = @JoinColumn(name = "crew_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "boat_id", referencedColumnName = "id"))
    private Set<Boat> ownedBoats;

    @Column(name = "roles", nullable = false)
    private String roles;

    @Column
    private CrewStatus status;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = EAGER)
    @JoinColumn(name = "crew_id")
    private Set<ChangeOwnerRequest> changeOwnerRequests;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
