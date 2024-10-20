local performanceKey = 'avgRating:'..ARGV[1]
local avgRating = ARGV[2]
local reviewCount = ARGV[3]

redis.call('HSET',performanceKey,'avgRating', avgRating)
redis.call('HSET',performanceKey,'reviewCount', reviewCount)

