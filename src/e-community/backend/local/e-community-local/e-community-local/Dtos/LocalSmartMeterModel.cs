using System;

namespace e_community_local.Dtos
{
    public class LocalSmartMeterModel
    {
        public Guid Id { get; set; }
        public string AESKey { get; set; }
        public string APIKey { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public bool IsMain { get; set; }
        public bool MeasuresConsumption { get; set; }
        public bool MeasuresFeedIn { get; set; }
        public bool IsDirectFeedIn { get; set; }
        public bool IsOverflowFeedIn { get; set; }
    }
}
