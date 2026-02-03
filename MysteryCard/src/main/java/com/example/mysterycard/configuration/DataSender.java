package com.example.mysterycard.configuration;

import com.example.mysterycard.entity.Permision;
import com.example.mysterycard.entity.Role;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.RoleCode;
import com.example.mysterycard.repository.PermisionRepo;
import com.example.mysterycard.repository.RoleRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSender implements CommandLineRunner {
    private final RoleRepo roleRepository;
    private final PermisionRepo permisionRepository;
    private final UsersRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.count() > 0) return; // tránh insert lại

        /* ================= PRIVILEGES ================= */

        Permision p1 = createPrivilege("USER_VIEW", "View user", "Xem người dùng");
        Permision p2 = createPrivilege("USER_MANAGE", "Manage user", "Quản lý người dùng");
        Permision p3 = createPrivilege("CARD_SELL", "Sell card", "Bán thẻ");
        Permision p4 = createPrivilege("CARD_BUY", "Buy card", "Mua thẻ");
        Permision p5 = createPrivilege("BLIND_BOX_OPEN", "Open blind box", "Mở túi mù");
        Permision p6 = createPrivilege("SYSTEM_ADMIN", "System admin", "Toàn quyền hệ thống");

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
        Role shipment = createRole(
                RoleCode.SHIPPER.toString(),
                "Shipper",
                "Người update status shipment",
                Set.of(p3,p4, p5)
        );

        /* ================= USERS ================= */

        createUserIfNotExist(
                "admin@mysterycard.com",
                "123456",
                "Admin System",
                "3695"
                ,"90752",
                Set.of(adminRole)
        );

        createUserIfNotExist(
                "seller@mysterycard.com",
                "123456",
                "Card Seller",
                "1694",
                "480802",
                Set.of(userRole)
        );

        createUserIfNotExist(
                "buyer@mysterycard.com",
                "123456",
                "Blind Box Buyer",
                "1482",
                "11003",
                Set.of(userRole)
        );
        createUserIfNotExist(
                "shipper@mysterycard.com",
                "123456",
                "Blind Box Buyer",
                "3695",
                "90760",
                Set.of(shipment)
        );

        System.out.println("✅ Data seeding completed!");
    };


/* ================= Helper methods ================= */

private Permision createPrivilege(String code, String name, String desc) {
    return permisionRepository.findById(code)
            .orElseGet(() -> permisionRepository.save(
                    new Permision(code, name, desc)
            ));
}

private Role createRole(String code, String name, String desc, Set<Permision> permisions) {
    return roleRepository.findById(code)
            .orElseGet(() -> {
                Role role = new Role();
                role.setRoleCode(code);
                role.setRoleName(name);
                role.setDescription(desc);
                role.setPermisions(permisions);
                role.setCreatedAt(LocalDateTime.now());
                role.setActive(true);
                return roleRepository.save(role);
            });
}

private void createUserIfNotExist(String email, String password, String name, String districtId, String wardId,Set<Role> roles) {
    if (userRepository.existsByEmail(email)) return;

    Users user = Users.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .active(true)
            .districtId(districtId)
            .wardId(wardId)
            .build();

    user.setRolelist(roles);
    userRepository.save(user);
    walletService.createWallet(user);

}
}
