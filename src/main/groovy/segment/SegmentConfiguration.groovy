package segment

import io.micronaut.context.annotation.ConfigurationProperties

import javax.validation.constraints.NotBlank

@ConfigurationProperties('segment')
class SegmentConfiguration {

    @NotBlank
    String apiKey
    // TODO should be enabled by default in PROD
    Boolean enabled = false
    Map options = [:]

}
