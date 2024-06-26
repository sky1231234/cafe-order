package com.example.order.cafe.service;

import com.example.order.cafe.domain.*;
import com.example.order.cafe.dto.request.CafeCreateRequest;
import com.example.order.cafe.dto.request.CafeUpdateRequest;
import com.example.order.cafe.dto.request.OperationTimeRequest;
import com.example.order.cafe.dto.request.TimeRequest;
import com.example.order.cafe.fixture.BusinessHoursFixture;
import com.example.order.cafe.fixture.CafeFixture;
import com.example.order.cafe.fixture.CafeInfoFixture;
import com.example.order.cafe.mapper.OperationTimeMapper;
import com.example.order.cafe.repository.CafeInfoRepository;
import com.example.order.cafe.repository.CafeRepository;
import com.example.order.member.errorCode.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CafeService 테스트")
public class CafeServiceTest {

    @Autowired
    private CafeService cafeService;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private CafeInfoRepository cafeInfoRepository;

    @Test
    @DisplayName("정상 : 영업시간 매핑 확인")
    public void toOperationTimeMapStructTest(){

        TimeRequest 평일_오픈 = new TimeRequest(9, 0);
        TimeRequest 평일_마감 = new TimeRequest(17, 0);

        OperationTimeRequest operationTimeRequest = BusinessHoursFixture.운영시간_REQUEST_DTO(평일_오픈, 평일_마감);

        OperationTime operationTime = OperationTimeMapper.INSTANCE.toOperationTime(operationTimeRequest);

        assertEquals(operationTime.getOpen().getHour(), Time.of(9,0).getHour());
        assertEquals(operationTime.getOpen().getMinute(), Time.of(9,0).getMinute());

    }

    @Test
    @DisplayName("정상 : 카페 등록")
    public void registerCafeTest(){

        CafeCreateRequest 카페_request = CafeFixture.카페_등록_REQUEST_DTO();

        Cafe 카페 = cafeService.registerCafe(카페_request);

        Optional<Cafe> 등록_카페 = cafeRepository.findById(카페.getId());
        CafeInfo 등록_카페_프로필 = cafeInfoRepository.findById(카페.getId())
                .orElseThrow(() -> new RuntimeException("카페 프로필이 존재하지 않습니다."));

        assertNotNull(등록_카페);
        assertNotNull(등록_카페_프로필);
        assertThat(등록_카페_프로필.getAddress()).isEqualTo(CafeInfoFixture.주소);

    }

    @Test
    @DisplayName("정상 : 카페 수정")
    public void updateCafeTest(){

        CafeCreateRequest 카페_request = CafeFixture.카페_등록_REQUEST_DTO();
        Cafe 카페 = cafeService.registerCafe(카페_request);
        CafeUpdateRequest 카페_수정_request = CafeFixture.카페_수정_REQUEST_DTO();

    }

    @Test
    @DisplayName("예외 : 카페 수정시 null값")
    public void error_cafe_info_is_null(){

        CafeCreateRequest cafeCreateRequest = CafeFixture.카페_등록_REQUEST_DTO();
        cafeService.registerCafe(cafeCreateRequest);

        assertThatRuntimeException()
                .isThrownBy(() -> cafeService.registerCafe(cafeCreateRequest))
                .withMessage(MemberErrorCode.ALREADY_EXIST_MEMBER_ID_EXCEPTION.getValue());

    }

}
