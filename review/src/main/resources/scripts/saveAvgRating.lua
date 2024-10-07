local countKey = 'performance:'..ARGV[1]..':rating_count'
local sumKey = 'performance:'..ARGV[1]..':rating_sum'

local currentCount = tonumber(redis.call('GET', countKey)) or 0
local currentSum = tonumber(redis.call('GET', sumKey)) or 0

local setCountResult = redis.call('SET', countKey, currentCount + tonumber(ARGV[2]))
local setSumResult = redis.call('SET', sumKey, currentSum + tonumber(ARGV[3]))

if setCountResult == 'OK' and setSumResult == 'OK' then
    return true
else
    return false
end