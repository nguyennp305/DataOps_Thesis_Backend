package com.khanh.labeling_management;

import com.khanh.labeling_management.entity.information_management.Role;
import com.khanh.labeling_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Slf4j
@RequiredArgsConstructor
public class CommandsLineRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeUserRole();
    }

    private void initializeUserRole() {
        if (!roleRepository.existsByName("Admin")) {
            Role role = new Role();
            role.setName("Admin");
            role.setCreatedAt(new Date());
            role.setUpdatedAt(new Date());
            roleRepository.save(role);
        }
    }

// TODO:
//    Đầu vào:
//    Danh sách tài liệu được gửi theo thời gian (document_id, category_id, send_at, completed_time),
//    completed_time là lượng thời gian để hoàn thành review, cứ hết đúng completed_time sẽ hoàn thành review mà không quan tâm kết quả review ra sao.
//    Danh sách người tham gia room review theo thời gian (user_id, category_id, join_at)
//

// TODO
//    Đầu ra
//    Danh sách user đã nhận được những bài viết nào: (user_id, list<document_id>)
//    Thứ tự ưu tiên gửi tài liệu từ cao xuống thấp
//    Ưu tiên gửi tài liệu cho người có cùng category_id
//    Ưu tiên theo thời gian người join room trước hoặc hoàn thành review gần nhất.
//    Mỗi người chỉ nhận tối đa 2 tài liệu một lúc.

}
