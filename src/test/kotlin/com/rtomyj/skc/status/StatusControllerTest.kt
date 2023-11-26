package com.rtomyj.skc.status

import com.rtomyj.skc.dao.StatusDao
import com.rtomyj.skc.exception.ErrorType
import com.rtomyj.skc.exception.SKCException
import com.rtomyj.skc.model.DownstreamStatus
import com.rtomyj.skc.model.SuggestionEngineDownstreamStatus
import com.rtomyj.skc.model.SuggestionEngineStatus
import com.rtomyj.skc.skcsuggestionengine.SuggestionEngineStatusService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(StatusController::class)
@ContextConfiguration(classes = [StatusController::class])
@Tag("Controller")
class StatusControllerTest {
  @Autowired
  private lateinit var mockMvc: WebTestClient

  @MockBean(name = "jdbc")
  private lateinit var dao: StatusDao

  @MockBean
  private lateinit var suggestionEngineStatusService: SuggestionEngineStatusService


  companion object {
    private const val DB_NAME = "SKC DB"
    private const val DB_VERSION = "8"
    private const val DB_STATUS = "up"

    private const val SKC_SUGGESTION_ENGINE_NAME = "SKC Suggestion Engine"
    private const val SKC_SUGGESTION_ENGINE_VERSION = "1.0.0"
    private const val SKC_SUGGESTION_ENGINE_STATUS = "All good 😛"

    private val SKC_DB_UP = DownstreamStatus(DB_NAME, DB_VERSION, DB_STATUS)
    private val SKC_SUGGESTION_ENGINE_DB_UP = SuggestionEngineDownstreamStatus("Suggestion Engine DB", "Up")
    private val SKC_SUGGESTION_ENGINE_MONGO_DB_UP =
      SuggestionEngineDownstreamStatus("Suggestion Engine MongoDB", "Down")
  }


  @Nested
  inner class HappyPath {
    @Test
    fun `Calling Test Call Endpoint With Get Method - Success`() {
      Mockito
          .`when`(dao.dbConnection())
          .thenReturn(SKC_DB_UP)

      Mockito
          .`when`(suggestionEngineStatusService.getStatus())
          .thenReturn(SuggestionEngineStatus(SKC_SUGGESTION_ENGINE_VERSION, listOf(SKC_SUGGESTION_ENGINE_DB_UP)))

      mockMvc
          .get()
          .uri("/status")
          .exchange()
          .expectStatus().isOk
          .expectBody()
          .jsonPath("$.status")
          .isEqualTo("API is online and functional")
          .jsonPath("$.downstream[0].name")
          .isEqualTo(DB_NAME)
          .jsonPath("$.downstream[0].version")
          .isEqualTo(DB_VERSION)
          .jsonPath("$.downstream[0].status")
          .isEqualTo(DB_STATUS)
          .jsonPath("$.downstream[1].name")
          .isEqualTo(SKC_SUGGESTION_ENGINE_NAME)
          .jsonPath("$.downstream[1].version")
          .isEqualTo(SKC_SUGGESTION_ENGINE_VERSION)
          .jsonPath("$.downstream[1].status")
          .isEqualTo(SKC_SUGGESTION_ENGINE_STATUS)
    }

    @Test
    fun `Calling Test Call Endpoint With Get Method - SKC Suggestion Engine Has One Service Down`() {
      Mockito
          .`when`(dao.dbConnection())
          .thenReturn(SKC_DB_UP)

      Mockito
          .`when`(suggestionEngineStatusService.getStatus())
          .thenReturn(
            SuggestionEngineStatus(
              SKC_SUGGESTION_ENGINE_VERSION, listOf(SKC_SUGGESTION_ENGINE_DB_UP, SKC_SUGGESTION_ENGINE_MONGO_DB_UP)
            )
          )

      mockMvc
          .get()
          .uri("/status")
          .exchange()
          .expectStatus().isOk
          .expectBody()
          .jsonPath("$.status")
          .isEqualTo("API is online and functional")
          .jsonPath("$.downstream[0].name")
          .isEqualTo(DB_NAME)
          .jsonPath("$.downstream[0].version")
          .isEqualTo(DB_VERSION)
          .jsonPath("$.downstream[0].status")
          .isEqualTo(DB_STATUS)
          .jsonPath("$.downstream[1].name")
          .isEqualTo(SKC_SUGGESTION_ENGINE_NAME)
          .jsonPath("$.downstream[1].version")
          .isEqualTo(SKC_SUGGESTION_ENGINE_VERSION)
          .jsonPath("$.downstream[1].status")
          .isEqualTo("The following services are offline: Suggestion Engine MongoDB")
    }

    @Test
    fun `Calling Test Call Endpoint With Get Method - SKC Suggestion Engine Status Results In An Error`() {
      Mockito
          .`when`(dao.dbConnection())
          .thenReturn(SKC_DB_UP)

      Mockito
          .`when`(suggestionEngineStatusService.getStatus())
          .thenThrow(SKCException("Suggestion Engine status check failed.", ErrorType.DS001))

      mockMvc
          .get()
          .uri("/status")
          .exchange()
          .expectStatus().isOk
          .expectBody()
          .jsonPath("$.status")
          .isEqualTo("API is online and functional")
          .jsonPath("$.downstream[0].name")
          .isEqualTo(DB_NAME)
          .jsonPath("$.downstream[0].version")
          .isEqualTo(DB_VERSION)
          .jsonPath("$.downstream[0].status")
          .isEqualTo(DB_STATUS)
          .jsonPath("$.downstream[1].name")
          .isEqualTo(SKC_SUGGESTION_ENGINE_NAME)
          .jsonPath("$.downstream[1].version")
          .isEqualTo("N/A")
          .jsonPath("$.downstream[1].status")
          .isEqualTo("down")
    }
  }
}