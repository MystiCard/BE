package com.example.mysterycard.configuration;

import com.example.mysterycard.entity.Privilege;
import com.example.mysterycard.entity.Role;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.repository.PrivilegeRepo;
import com.example.mysterycard.repository.RoleRepo;
import com.example.mysterycard.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSender implements CommandLineRunner {
    private final RoleRepo roleRepository;
    private final PrivilegeRepo privilegeRepository;
    private final UsersRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.count() > 0) return; // tránh insert lại

        /* ================= PRIVILEGES ================= */

        Privilege p1 = createPrivilege("USER_VIEW", "View user", "Xem người dùng");
        Privilege p2 = createPrivilege("USER_MANAGE", "Manage user", "Quản lý người dùng");
        Privilege p3 = createPrivilege("CARD_SELL", "Sell card", "Bán thẻ");
        Privilege p4 = createPrivilege("CARD_BUY", "Buy card", "Mua thẻ");
        Privilege p5 = createPrivilege("BLIND_BOX_OPEN", "Open blind box", "Mở túi mù");
        Privilege p6 = createPrivilege("SYSTEM_ADMIN", "System admin", "Toàn quyền hệ thống");

        /* ================= ROLES ================= */

        Role adminRole = createRole(
                "ADMIN",
                "Administrator",
                "Quản trị hệ thống",
                Set.of(p1, p2, p3, p4, p5, p6)
        );

        Role userRole = createRole(
                "USER",
                "user",
                "Người mua thẻ/ ban thẻ",
                Set.of(p3,p4, p5)
        );

        /* ================= USERS ================= */

        createUserIfNotExist(
                "admin@mysterycard.com",
                "123456",
                "Admin System",
                Set.of(adminRole)
        );

        createUserIfNotExist(
                "seller@mysterycard.com",
                "123456",
                "Card Seller",
                Set.of(userRole)
        );

        createUserIfNotExist(
                "buyer@mysterycard.com",
                "123456",
                "Blind Box Buyer",
                Set.of(userRole)
        );

        System.out.println("✅ Data seeding completed!");
    };


/* ================= Helper methods ================= */

private Privilege createPrivilege(String code, String name, String desc) {
    return privilegeRepository.findById(code)
            .orElseGet(() -> privilegeRepository.save(
                    new Privilege(code, name, desc)
            ));
}

private Role createRole(String code, String name, String desc, Set<Privilege> privileges) {
    return roleRepository.findById(code)
            .orElseGet(() -> {
                Role role = new Role();
                role.setRoleCode(code);
                role.setRoleName(name);
                role.setDescription(desc);
                role.setPrivileges(privileges);
                return roleRepository.save(role);
            });
}

private void createUserIfNotExist(String email, String password, String name, Set<Role> roles) {
    if (userRepository.existsByEmail(email)) return;

    Users user = Users.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .isActive(true)
            .build();

    user.setRolelist(roles);
    userRepository.save(user);

}
}
