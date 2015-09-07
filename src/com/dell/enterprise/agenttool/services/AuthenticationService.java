package com.dell.enterprise.agenttool.services;

import com.dell.enterprise.agenttool.DAO.AuthenticationDAO;
import com.dell.enterprise.agenttool.model.Agent;

/**
 * @author hungnguyen
 * 
 * @version $Id$
 **/
public class AuthenticationService
{
    /**
     * @param userName
     *            self-explained
     * @param password
     *            self-explained
     * @return Agent
     **/
    public Agent authenticate(final String userName, final String password)
    {
        AuthenticationDAO dao = new AuthenticationDAO();
        Agent agent = dao.authenticate(userName, password);

        return agent;
    }
    
    /**
     * @param userName
     *            self-explained
     * @param password
     *            self-explained
     * @return Agent
     **/
    public Agent login(final String userName, final String password)
    {
        AuthenticationDAO dao = new AuthenticationDAO();
        Agent agent = dao.login(userName, password);

        return agent;
    }
    
    public Agent getAgentByAgentId(int agent_id)
    {
        AuthenticationDAO dao = new AuthenticationDAO();
        Agent agent = dao.getAgentByAgentId(agent_id);
        return agent;
    }
}
