package nextstep.subway.favorite.aceeptance;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.acceptance.AuthAcceptanceTest;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.favorite.ui.FavoriteControllerTest;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.acceptance.LineSectionAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.member.MemberAcceptanceTest;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("즐겨찾기 관련 인수테스트")
public class FavoriteAcceptanceTest extends AcceptanceTest {
    private StationResponse 양재역;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 교대역;
    private StationResponse 우성역;

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;

    private MemberRequest 테스트계정;
    private String accessJwt;
    private FavoriteRequest favoriteRequest;
    private FavoriteResponse expectedfavoriteResponse;

    @BeforeEach
    public void setUp() {
        super.setUp();

        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        역삼역 = StationAcceptanceTest.지하철역_등록되어_있음("역삼역").as(StationResponse.class);
        교대역 = StationAcceptanceTest.지하철역_등록되어_있음("교대역").as(StationResponse.class);

        LineRequest 신분당선_노선등록 = new LineRequest("신분당선", "bg-blue-600", 강남역.getId(), 양재역.getId(), 30);
        LineRequest 이호선_노선등록 = new LineRequest("이호선", "bg-green-600", 교대역.getId(), 강남역.getId(), 10);
        LineRequest 삼호선_노선등록 = new LineRequest("삼호선", "bg-orange-600", 교대역.getId(), 양재역.getId(), 50);

        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(신분당선_노선등록).as(LineResponse.class);
        이호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(이호선_노선등록).as(LineResponse.class);
        삼호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(삼호선_노선등록).as(LineResponse.class);

        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(이호선, 강남역, 역삼역, 100);
        테스트계정 = new MemberRequest("probitanima11@gmail.com", "11", 10);
        MemberAcceptanceTest.회원_생성을_요청(테스트계정.getEmail(), 테스트계정.getPassword(), 테스트계정.getAge());

        ExtractableResponse<Response> correctAccountResponse = AuthAcceptanceTest.JWT_요청(테스트계정);
        accessJwt = MemberAcceptanceTest.JWT_받음(correctAccountResponse);

        favoriteRequest = FavoriteRequest.of(교대역.getId(), 양재역.getId());

        expectedfavoriteResponse = FavoriteResponse.of(1L, 교대역 , 양재역);
    }

    @DisplayName("즐겨찾기 관리")
    @Test
    void TotalFavoriteAcceptance() {
        // when
        ExtractableResponse<Response> createdFavoriteResponse = FavoriteControllerTest.즐겨찾기_생성_요청(accessJwt, favoriteRequest);
        // then
        FavoriteControllerTest.즐겨찾기_생성됨(createdFavoriteResponse);

        // when
        ExtractableResponse<Response> searchedFavoriteResponse = FavoriteControllerTest.즐겨찾기_목록조회_요청(accessJwt);
        // then
        FavoriteControllerTest.즐겨찾기_목록조회됨(searchedFavoriteResponse, expectedfavoriteResponse);

        // given
        FavoriteResponse[] favoriteResponse = searchedFavoriteResponse.as(FavoriteResponse[].class);
        // when
        ExtractableResponse<Response> deletedFavoriteResponse = FavoriteControllerTest.즐겨찾기_삭제_요청(accessJwt, favoriteResponse[0]);
        // then
        FavoriteControllerTest.즐겨찾기_삭제됨(deletedFavoriteResponse);

        // when
        ExtractableResponse<Response> searchedFavoriteResponseAfterDelete = FavoriteControllerTest.즐겨찾기_목록조회_요청(accessJwt);
        // then
        삭제후_즐겨찾기_목록조회됨(searchedFavoriteResponseAfterDelete);
    }

    private void 삭제후_즐겨찾기_목록조회됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.as(FavoriteResponse[].class)).isEmpty();
    }
}