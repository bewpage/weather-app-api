package com.weatherapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OSMResponseDto {
  @JsonProperty("place_id")
  private String place_id;

  @JsonProperty("osm_type")
  private String osm_type;

  @JsonProperty("osm_id")
  private String osm_id;

  @JsonProperty("lat")
  private Double latitude;

  @JsonProperty("lon")
  private Double longitude;

  @JsonProperty("display_name")
  private String display_name;

  @JsonProperty("type")
  private String type;
}
