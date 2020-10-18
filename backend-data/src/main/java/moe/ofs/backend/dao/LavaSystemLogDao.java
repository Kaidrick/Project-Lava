package moe.ofs.backend.dao;

import moe.ofs.backend.domain.LavaSystemLog;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
public class LavaSystemLogDao {
    private final SqlSession sqlSession;

    public LavaSystemLogDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public LavaSystemLog selectById(long id) {
        System.out.println(id);
        System.out.println(sqlSession.selectOne("selectById", id).toString());
        return sqlSession.selectOne("selectById", id);
    }
}
