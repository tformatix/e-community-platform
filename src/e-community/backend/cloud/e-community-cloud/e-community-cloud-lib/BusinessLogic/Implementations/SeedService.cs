using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Database.Community;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.BusinessLogic.Implementations
{
    public class SeedService : ISeedService
    {
        private readonly ECommunityCloudContext mDb;

        public SeedService(ECommunityCloudContext _db)
        {
            this.mDb = _db;
        }

        /// <summary>
        /// add default values to the general tables (e_community_type, language, legal_form)
        /// </summary>
        public void AddGeneralTables()
        {
            // Language
            mDb.Language.Add(new Language() { Name = "de" });
            mDb.Language.Add(new Language() { Name = "en" });

            // Legal Form
            mDb.LegalForm.Add(new LegalForm() { Name = "GmbH" });
            mDb.LegalForm.Add(new LegalForm() { Name = "Gemeinde" });
            mDb.LegalForm.Add(new LegalForm() { Name = "OG" });
            mDb.LegalForm.Add(new LegalForm() { Name = "KG" });
            mDb.LegalForm.Add(new LegalForm() { Name = "GmbH & Co. KG" });
            mDb.LegalForm.Add(new LegalForm() { Name = "AG" });
            mDb.LegalForm.Add(new LegalForm() { Name = "Normal-Member" });

            // ECommunity Type
            mDb.ECommunityType.Add(new ECommunityType()
            {
                Name = "EEG",
                DiscountLocal = 57.0,
                DiscountLowRegional = 28.0,
                DiscountHighRegional = 64.0
            });

            mDb.ECommunityType.Add(new ECommunityType()
            {
                Name = "BEG",
                DiscountLocal = 0.0,
                DiscountLowRegional = 0.0,
                DiscountHighRegional = 0.0
            });

            mDb.SaveChanges();
        }

        /// <summary>
        /// delete values of the general tables (e_community_type, language, legal_form)
        /// </summary>
        public void RemoveGeneralTables()
        {
            // delete Language
            mDb.Language.RemoveRange(mDb.Language);

            // delete ECommunityType
            mDb.ECommunityType.RemoveRange(mDb.ECommunityType);

            // delete LegalForm
            mDb.LegalForm.RemoveRange(mDb.LegalForm);

            mDb.SaveChanges();
        }
    }
}
