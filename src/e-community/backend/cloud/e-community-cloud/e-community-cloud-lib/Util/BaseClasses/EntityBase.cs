using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace e_community_cloud_lib.Util.BaseClasses
{
    /// <summary>
    /// base class of a entity (stores an Id in Guid)
    /// </summary>
    public class EntityBase
    {
        /// <summary>
        /// PK - Guid automatic generated
        /// </summary>
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public Guid Id { get; set; }
    }
}
