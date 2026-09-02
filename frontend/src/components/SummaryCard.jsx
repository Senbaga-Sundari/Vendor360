import {
    Card,
    CardContent,
    Typography
} from "@mui/material";

function SummaryCard({ title, value }) {

    return (
        <Card
            sx={{
                borderRadius: 3,
                minWidth: 200,
                boxShadow: 3
            }}
        >
            <CardContent>

                <Typography
                    variant="body2"
                    color="text.secondary"
                >
                    {title}
                </Typography>

                <Typography
                    variant="h4"
                    sx={{
                        marginTop: 1,
                        fontWeight: "bold"
                    }}
                >
                    {value}
                </Typography>

            </CardContent>
        </Card>
    );
}

export default SummaryCard;