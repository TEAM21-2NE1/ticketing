local countKey = 'performance:'..ARGV[1]..':rating_count'
local sumKey = 'performance:'..ARGV[1]..':rating_sum'

local currentCount = tonumber(redis.call('GET', countKey)) or 0
local currentSum = tonumber(redis.call('GET', sumKey)) or 0

if currentCount ~= 0 then
  return tostring( currentSum / currentCount)
else
  return nil
end