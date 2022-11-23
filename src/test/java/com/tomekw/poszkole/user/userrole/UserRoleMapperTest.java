package com.tomekw.poszkole.user.userrole;

import com.tomekw.poszkole.exceptions.globalexceptionhandler.RequestedUserRolesNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleMapperTest {

    @InjectMocks
    private UserRoleMapper userRoleMapper;
    @Mock
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        when(userRoleRepository.findRole(anyString())).thenAnswer(invocationOnMock -> {
                    String roleName = invocationOnMock.getArgument(0);

                    if (roleName.equals("ADMIN")) {
                        return Optional.of(new UserRole());
                    }
                    if (roleName.equals("TEACHER")) {
                        return Optional.of(new UserRole());
                    }
                    return Optional.empty();
                }
        );
    }

    @Test
    void mapToUserRoleList_shouldReturnListOfRoles_whenAllGivenRolesAreFoundInRepo() {
        //given
        List<String> inputRoles = List.of("ADMIN", "TEACHER");

        //when
        List<UserRole> outputRoles = userRoleMapper.mapToUserRoleList(inputRoles);

        //then
        assertThat(outputRoles).hasSize(2);
    }

    @Test
    void mapToUserRoleList_shouldThrowRequestedUserRolesNotFoundException_atLeastOneGivenRoleIsNotFoundInRepo() {
        //given
        List<String> inputRoles = List.of("asd");

        //when

        //then
        assertThrows(RequestedUserRolesNotFoundException.class, () -> userRoleMapper.mapToUserRoleList(inputRoles));
    }
}