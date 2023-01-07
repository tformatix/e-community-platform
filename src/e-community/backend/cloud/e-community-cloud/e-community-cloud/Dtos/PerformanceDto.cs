namespace e_community_cloud.Dtos {
    public class PerformanceDto {
        /// <summary>
        /// number of relevant forecasts
        /// </summary>
        public int ForecastCount { get; set; }

        /// <summary>
        /// how many forecasts were good (in range)
        /// </summary>
        public int GoodForecastCount { get; set; }

        /// <summary>
        /// how much energy was wrongly forecasted
        /// </summary>
        public int WrongForecasted { get; set; }
    }
}
