-- 读取当前票档库存
local stock = redis.call('GET', KEYS[1])

-- Redis中还没有初始化该票档库存
if not stock then
    return -1
end

local quantity = tonumber(ARGV[1])

-- 购买数量不合法
if not quantity or quantity <= 0 then
    return -2
end

stock = tonumber(stock)

-- 库存不足
if stock < quantity then
    return 0
end

-- 库存充足，执行原子扣减
redis.call('DECRBY', KEYS[1], quantity)

return 1