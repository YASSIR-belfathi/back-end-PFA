package rag_system.demo.tools;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service("schoolIdentityInfo")
@Description("""
        Get Identity Info about a given School icluding :
        - The Name of the school
        - The City of the school
        - The Branches of the school
        - The founded year of the school
        """)
public class SchoolIdentityInfo
        implements Function<SchoolIdentityInfo.Request, SchoolIdentityInfo.Response> {

    public record Response(
            String schoolName,
            String city,
            String branches,
            int foundedYear){};

    public record Request(
            String schoolName
    ){};

    @Override
    public Response apply(Request request) {
        return new Response(request.schoolName(), "benimellal","TDI-EREE-SCIA-IAA",2019);
    }


}
