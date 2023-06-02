using System.Collections.Generic;
using System.Linq;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database.Community;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Util.Extensions;
using e_community_cloud.Dtos;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace e_community_cloud.Controllers;

[ApiController]
[Route("[controller]/[action]")]
public class SearchController : ControllerBase
{
    private readonly ISearchService mSearchService;

    public SearchController(ISearchService _searchService)
    {
        mSearchService = _searchService;
    }

    [ProducesResponseType(typeof(List<MemberDto>), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpGet]
    public List<MemberDto> SearchForUser(string _query)
    {
        var foundUsers = mSearchService.SearchForUsers(_query);

        var listOfDtos = foundUsers.Select(user => user.CopyPropertiesTo(new MemberDto())).ToList();

        return listOfDtos;
    }
    
    [ProducesResponseType(typeof(List<ECommunityDto>), 200)]
    [ProducesResponseType(typeof(ErrorDto), 400)]
    [Authorize]
    [HttpGet]
    public List<ECommunityDto> SearchForEComms(string _query)
    {
        var foundEComms = mSearchService.SearchForEComms(_query);

        var listOfDtos = foundEComms.Select(user => user.CopyPropertiesTo(new ECommunityDto())).ToList();

        return listOfDtos;
    }
}