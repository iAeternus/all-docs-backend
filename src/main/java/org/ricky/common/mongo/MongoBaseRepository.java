package org.ricky.common.mongo;

import com.google.common.collect.Maps;
import org.ricky.core.common.context.ThreadLocalContext;
import org.ricky.core.common.domain.AggregateRoot;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventDao;
import org.ricky.common.event.publish.interception.ThreadLocalDomainEventIdHolder;
import org.ricky.common.exception.MyException;
import org.ricky.common.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.common.exception.ErrorCodeEnum.*;
import static org.ricky.common.util.ValidationUtil.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className MongoBaseRepository
 * @desc 数据仓库抽象基类 <br>
 * 关于BaseRepository及其子类的约定：<br>
 * 1. 后缀为byXXX的方法，不会做checkUserShip检查，在没找到资源时将抛出异常<br>
 * 2. 后缀为byXxxOptional的方法，不会做checkUserShip检查，在没找到资源时返回empty()<br>
 * 3. 后缀为byXxxAndCheckUserShip的方法，会做checkUserShip检查，在没找到资源时将抛出异常<br>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MongoBaseRepository<AR extends AggregateRoot> {

    /**
     * 数据仓库管理的聚合根
     */
    private final Map<String, Class> classMapper = Maps.newHashMap();

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected DomainEventDao domainEventDao;

    /**
     * 保存聚合根<br>
     * 文档已存在则更新，如果不存在则插入<br>
     * 如果聚合根内有领域事件，会持久化并清空，给聚合根瘦身<br>
     *
     * @param ar 聚合根
     */
    @Transactional
    public void save(AR ar) {
        requireNonNull(ar, "AggregateRoot must not be null.");

        if (isNotEmpty(ar.getEvents())) {
            saveEvents(ar.getEvents());
            ar.clearEvents();
        }

        mongoTemplate.save(ar);
    }

    /**
     * 批量保存聚合根<br>
     * 文档已存在则更新，如果不存在则插入<br>
     * 如果集合为空则什么都不做<br>
     * 同样会持久化领域事件并清空每个聚合根的事件列表<br>
     *
     * @param ars 聚合根集合
     */
    @Transactional
    public void save(List<AR> ars) {
        if (isEmpty(ars)) {
            return;
        }

        checkSameUser(ars);
        List<DomainEvent> events = new ArrayList<>();
        ars.forEach(ar -> {
            if (isNotEmpty(ar.getEvents())) {
                events.addAll(ar.getEvents());
                ar.clearEvents();
            }
            mongoTemplate.save(ar);
        });

        saveEvents(events);
    }

    /**
     * 保存聚合根<br>
     * 如果聚合根ID已存在则抛出异常<br>
     * 同样会持久化领域事件并清空每个聚合根的事件列表<br>
     *
     * @param ar 聚合根
     */
    @Transactional
    public void insert(AR ar) {
        requireNonNull(ar, "AggregateRoot must not be null.");

        if (isNotEmpty(ar.getEvents())) {
            saveEvents(ar.getEvents());
            ar.clearEvents();
        }

        mongoTemplate.insert(ar);
    }

    /**
     * 保存聚合根集合<br>
     * 如果聚合根ID已存在则抛出异常<br>
     * 如果集合为空则什么都不做<br>
     * 同样会持久化领域事件并清空每个聚合根的事件列表<br>
     *
     * @param ars 聚合根集合
     */
    @Transactional
    public void insert(List<AR> ars) {
        if (isEmpty(ars)) {
            return;
        }

        checkSameUser(ars);
        List<DomainEvent> events = new ArrayList<>();
        ars.forEach(ar -> {
            if (isNotEmpty(ar.getEvents())) {
                events.addAll(ar.getEvents());
                ar.clearEvents();
            }
        });

        mongoTemplate.insertAll(ars);
        saveEvents(events);
    }

    /**
     * 删除聚合根<br>
     * 删除前会持久化聚合根携带的所有领域事件<br>
     *
     * @param ar 聚合根
     */
    @Transactional
    public void delete(AR ar) {
        requireNonNull(ar, "AggregateRoot must not be null.");

        if (isNotEmpty(ar.getEvents())) {
            saveEvents(ar.getEvents());
            ar.clearEvents();
        }

        mongoTemplate.remove(ar);
    }

    /**
     * 批量删除聚合根<br>
     * 如果聚合根集合为空则什么都不做<br>
     * 删除前会持久化聚合根携带的所有领域事件<br>
     *
     * @param ars 聚合根集合
     */
    @Transactional
    public void delete(List<AR> ars) {
        if (isEmpty(ars)) {
            return;
        }
        checkSameUser(ars);

        List<DomainEvent> events = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        ars.forEach(ar -> {
            if (isNotEmpty(ar.getEvents())) {
                events.addAll(ar.getEvents());
                ar.clearEvents();
            }
            ids.add(ar.getId());
        });

        saveEvents(events);
        mongoTemplate.remove(query(where("_id").in(ids)), getType());
    }

    /**
     * 根据ID获取聚合根<br>
     * 不会做checkUserShip检查<br>
     * 没找到资源则抛出异常<br>
     *
     * @param id 聚合根ID
     * @return 聚合根
     */
    public AR byId(String id) {
        requireNotBlank(id, "AggregateRoot ID must not be blank.");

        Object ar = mongoTemplate.findById(id, getType());
        if (isNull(ar)) {
            throw new MyException(AR_NOT_FOUND, "未找到资源。", of("type", getType().getSimpleName(), "id", id));
        }

        return (AR) ar;
    }

    /**
     * 根据ID获取聚合根<br>
     * 不会做checkUserShip检查<br>
     * 没找到资源则返回Optional.empty()<br>
     *
     * @param id 聚合根ID
     * @return 聚合根
     */
    public Optional<AR> byIdOptional(String id) {
        requireNotBlank(id, "AggregateRoot ID must not be blank.");

        Object ar = mongoTemplate.findById(id, getType());
        return isNull(ar) ? Optional.empty() : Optional.of((AR) ar);
    }

    /**
     * 根据ID获取聚合根<br>
     * 会做checkUserShip检查<br>
     * 没找到资源则抛出异常<br>
     *
     * @param id 聚合根ID
     * @return 聚合根
     */
    public AR byIdAndCheckUserShip(String id) {
        requireNotBlank(id, "AggregateRoot ID must not be blank.");

        AR ar = byId(id);
        checkUserShip(ar);
        return ar;
    }

    /**
     * 根据ID集合批量获取聚合根<br>
     * 若ID集合为空则返回空列表<br>
     * 不会做checkUserShip检查<br>
     * 没找到资源则抛出异常<br>
     *
     * @param ids 聚合根ID集合
     * @return 聚合根集合
     */
    public List<AR> byIds(Set<String> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }

        List<AR> ars = mongoTemplate.find(query(where("_id").in(ids)), getType());
        checkSameUser(ars);
        return List.copyOf(ars);
    }

    /**
     * 根据ID集合批量获取聚合根<br>
     * 会做checkUserShip检查<br>
     * 没找到资源则抛出异常<br>
     *
     * @param ids 聚合根ID集合
     * @return 聚合根集合
     */
    public List<AR> byIdsAndCheckUserShip(Set<String> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }

        List<AR> ars = byIds(ids);
        ars.forEach(this::checkUserShip);
        return List.copyOf(ars);
    }

    /**
     * 根据ID集合批量获取聚合根<br>
     * 若ID集合为空则返回空列表<br>
     * 不会做checkUserShip检查<br>
     * 没找到资源或者没有找全资源均抛出异常<br>
     *
     * @param ids 聚合根ID集合
     * @return 聚合根集合
     */
    public List<AR> byIdsAll(Set<String> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }

        List<AR> ars = byIds(ids);
        if (ars.size() != ids.size()) {
            Set<String> fetchedIds = ars.stream()
                    .map(AggregateRoot::getId)
                    .collect(toImmutableSet());
            Set<String> originalIds = new HashSet<>(ids);
            originalIds.removeAll(fetchedIds);
            throw new MyException(AR_NOT_FOUND_ALL, "未找到所有资源。",
                    of("type", getType().getSimpleName(), "missingIds", originalIds));
        }
        return List.copyOf(ars);
    }

    /**
     * 根据ID集合批量获取聚合根<br>
     * 会做checkUserShip检查<br>
     * 没找到资源或者没有找全资源均抛出异常<br>
     *
     * @param ids 聚合根ID集合
     * @return 聚合根集合
     */
    public List<AR> byIdsAllAndCheckUserShip(Set<String> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }

        List<AR> ars = byIdsAll(ids);
        ars.forEach(this::checkUserShip);
        return List.copyOf(ars);
    }

    /**
     * 查询聚合根的个数
     *
     * @return 个数
     */
    public long count() {
        return mongoTemplate.count(new Query(), getType());
    }

    /**
     * 判断聚合根是否存在
     *
     * @param arId 聚合根ID
     * @return true=存在 false=不存在
     */
    public boolean exists(String arId) {
        requireNotBlank(arId, "AggregateRoot ID must not be blank.");

        Query query = query(where("_id").is(arId));
        return mongoTemplate.exists(query, getType());
    }

    /**
     * 持久化领域事件，如果入参为空则什么都不做
     *
     * @param events 领域事件集合
     */
    private void saveEvents(List<DomainEvent> events) {
        if (isNotEmpty(events)) {
            domainEventDao.insert(events);
            ThreadLocalDomainEventIdHolder.addEvents(events);
        }
    }

    /**
     * 检查所有聚合根的用户ID是否一致，若不一致则抛出异常
     *
     * @param ars 聚合根集合
     */
    private void checkSameUser(Collection<AR> ars) {
        Set<String> uids = ars.stream().map(AR::getUserId).collect(toImmutableSet());
        if (uids.size() > 1) {
            Set<String> allArIds = ars.stream().map(AggregateRoot::getId).collect(toImmutableSet());
            throw new MyException(SYSTEM_ERROR, "All AggregateRoots should belong to the same tenant.", "arIds", allArIds);
        }
    }

    /**
     * 获取当前数据仓库管理的聚合根的Class
     * 不存在则将当前数据仓库管理的聚合根Class加入到容器中
     *
     * @return 当前数据仓库管理的聚合根的Class
     */
    private Class getType() {
        String className = getClass().getSimpleName();

        if (!classMapper.containsKey(className)) {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            classMapper.put(className, (Class) actualTypeArguments[0]);
        }

        return classMapper.get(className);
    }

    /**
     * 检查用户关系<br>
     * 若聚合根的用户ID与用户ID不一致则抛出异常
     *
     * @param ar 聚合根
     */
    protected final void checkUserShip(AggregateRoot ar) {
        requireNonNull(ar, "AggregateRoot must not be null.");

        if (!ValidationUtil.equals(ar.getUserId(), ThreadLocalContext.getContext().getUid())) {
            throw new MyException(AR_NOT_FOUND, "未找到资源。", of("id", ar.getId(), "User ID", ar.getUserId()));
        }
    }

}
