-- 读取当前票档库存
local stock = redis.call('GET', KEYS[1])

-- Key不存在时不创建错误的库存数据
if not stock then
    return 0
end

local quantity = tonumber(ARGV[1])

-- 恢复数量不合法
if not quantity or quantity <= 0 then
    return -1
end

-- 原子恢复库存
redis.call('INCRBY', KEYS[1], quantity)

return 1