package me.kkw.springboot_developer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void cleanUp() {
        memberRepository.deleteAll();
    }

    @Sql("/insert-members.sql")
    @Test
    void getAllMembers() {
        List<Member> members = memberRepository.findAll();


        Assertions.assertThat(members.size()).isEqualTo(3);

    }

    @Sql("/insert-members.sql")
    @Test
    void getMemberById() {
        Member member = memberRepository.findById(2L).get();

        Assertions.assertThat(member.getName()).isEqualTo("B");
    }

    @Sql("/insert-members.sql")
    @Test
    void getMemberByName() {
        Member member = memberRepository.findByName("C").get();

        Assertions.assertThat(member.getId()).isEqualTo(3);
    }

    @Test
    void saveMember() {
        Member member = new Member(null, "A");

        memberRepository.save(member);

        Assertions.assertThat(memberRepository.findById(1L).get().getName()).isEqualTo("A");
    }

    @Test
    void saveMembers() {
        List<Member> members = List.of(new Member(null, "B"), new Member(null, "C"));

        memberRepository.saveAll(members);

        Assertions.assertThat(memberRepository.findAll().size()).isEqualTo(2);
    }

    @Sql("/insert-members.sql")
    @Test
    void deleteMemberById() {
        memberRepository.deleteById(2L);

        Assertions.assertThat(memberRepository.findById(2L).isEmpty()).isTrue();
    }

    @Sql("/insert-members.sql")
    @Test
    void deleteAll() {
        memberRepository.deleteAll();

        Assertions.assertThat(memberRepository.findAll().isEmpty()).isTrue();
    }

    @Sql("/insert-members.sql")
    @Test
    @Transactional
    void update() {
        Member member = memberRepository.findById(2L).get();

        member.changeName("BC");

        Assertions.assertThat(memberRepository.findById(2L).get().getName()).isEqualTo("BC");
    }
}