using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace e_community_cloud_lib.Util.Extensions
{
    public static class DtoExtensions
    {
        /// <summary>
        /// dynamically parses a object - called like source.CopyPropertiesTo(new ObjectA())
        /// </summary>
        public static T CopyPropertiesTo<T>(
          this object _source,
          T _target,
          string[] _ignoreProperties = null)
        {
            if (_ignoreProperties == null) _ignoreProperties = new string[] { };
            var propsSource = _source.GetType().GetProperties()
              .Where(x => x.CanRead && !_ignoreProperties.Contains(x.Name));
            var propsTarget = _target.GetType().GetProperties()
              .Where(x => x.CanWrite);

            propsTarget
              .Where(prop => propsSource.Any(x => x.Name == prop.Name))
              .ToList()
              .ForEach(prop =>
              {
                  var propSource = propsSource.Where(x => x.Name == prop.Name).First();
                  prop.SetValue(_target, propSource.GetValue(_source));
              });
            return _target;
        }
    }
}
